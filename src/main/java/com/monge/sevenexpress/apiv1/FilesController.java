
package com.monge.sevenexpress.apiv1;

import com.monge.sevenexpress.dto.ApiResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
public class FilesController {

    //-> file:/root/sevenexpress/files/
    @Value("${file.upload-dir}")
    private String baseUploadDir;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(
            @RequestParam("image") MultipartFile file,
            @RequestParam("folder") String folderName
    ) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            // Normaliza la ruta base (remueve "file:" si existe)
            String normalizedBaseDir = baseUploadDir.replace("file:/", "");
            
            // Crea la carpeta si no existe
            Path uploadPath = Paths.get(normalizedBaseDir, folderName);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Genera un nombre único
            String filename = UUID.randomUUID().toString() + getExtension(file.getOriginalFilename());

            Path filePath = uploadPath.resolve(filename);

            // Guarda el archivo
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // URL pública para descargar el archivo
            String fileUrl = "/api/v1/files/" + folderName + "/" + filename;

            Map<String, String> response = new HashMap<>();
            response.put("filename", filename);
            response.put("url", fileUrl);

            return ResponseEntity.ok(ApiResponse.success("success", response));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body(ApiResponse.error(e.getMessage()));
        }
    }



    /***
     *   //-> file:/root/sevenexpress/files/
     * @param folder
     * @param filename
     * @return 
     */
    @GetMapping("/{folder}/{filename:.+}")
    public ResponseEntity<Resource> downloadImage(
            @PathVariable String folder,
            @PathVariable String filename
    ) {
        try {
            // Normaliza la ruta base (remueve "file:" si existe)
            String normalizedBaseDir = baseUploadDir.replace("file:/", "");//-> root/sevenexpress/files/
            
            // Construye la ruta completa al archivo
            Path filePath = Paths.get(normalizedBaseDir, folder, filename).normalize();
            System.out.println("Intentando acceder a: " + filePath.toString());

            Resource resource = new UrlResource(filePath.toUri());
            
            // Verifica si el recurso existe y es legible
            if (!resource.exists() || !resource.isReadable()) {
                System.out.println("El recurso no existe o no se puede leer");
                return ResponseEntity.notFound().build();
            }

            // Determina el Content-Type dinámicamente
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (IOException e) {
         //   e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
        private String getExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf(".");
        return (dotIndex == -1) ? "" : filename.substring(dotIndex);
    }
}
