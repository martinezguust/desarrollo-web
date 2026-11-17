package mx.uv.fei.jurismagna;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import mx.uv.fei.jurismagna.servicio.ArchivoServicio;

// Sirve los archivos subidos (fotos, audios, documentos) en /uploads/...
@Configuration
public class ConfiguracionWeb implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registro) {
        registro.addResourceHandler("/uploads/**")
                .addResourceLocations(ArchivoServicio.CARPETA_UPLOADS.toUri().toString());
    }
}
