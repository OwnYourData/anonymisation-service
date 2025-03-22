package eu.ownyourdata.reasonerservice

import eu.ownyourdata.anonymisationservice.AnonymisationServiceController
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenAPIConfiguration {

    @Bean
    fun defineOpenAPI(): OpenAPI {
        val localServer = Server().apply {
            url = "http://localhost:8081"
            description = "Local_Server"
        }

        val kubernetesServer = Server().apply {
            url = "https://anonymizer.go-data.at"
            description = "Kubernetes_Server"
        }

        val information = Info().apply {
            title = "Anonymizer Service"
            version = AnonymisationServiceController.VERSION
            description = "API to anonymize input data"
        }

        return OpenAPI().info(information).servers(listOf(localServer, kubernetesServer))
    }
}
