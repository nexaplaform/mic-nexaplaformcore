package com.nexaplaform.core.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.reflections.Reflections;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.lang.reflect.Method;
import java.util.*;

@Configuration
public class ApiConfiguration {

    @Value("${nexaplaform.openapi.scan-package}")
    private String basePackageToScan;

    @Bean
    public OpenApiCustomizer globalResponseCustomizer() {
        return openApi -> {
            openApi.getPaths().forEach((path, pathItem) -> {
                pathItem.readOperations().forEach(operation -> {
                    String operationId = operation.getOperationId();
                    Optional<Method> methodOpt = findMethodByOperationId(operationId);
                    ApiResponses responses = operation.getResponses();

                    addIfMissing(responses, "401", "Unauthorized");
                    addIfMissing(responses, "403", "Forbidden");
                    addIfMissing(responses, "500", "Internal server error");

                    if (methodOpt.isPresent() && has404Mapping(methodOpt.get())) {
                        addIfMissing(responses, "404", "Not found");
                    }
                });
            });
        };
    }


    private boolean has404Mapping(Method method) {

        if (hasMappingOnMethod(method)) {
            return true;
        }

        Class<?> declaringClass = method.getDeclaringClass();
        for (Class<?> face : declaringClass.getInterfaces()) {
            try {
                Method interfaceMethod = face.getMethod(method.getName(), method.getParameterTypes());
                if (hasMappingOnMethod(interfaceMethod)) {
                    return true;
                }
            } catch (NoSuchMethodException ignored) {
            }
        }

        return false;
    }

    private boolean hasMappingOnMethod(Method method) {
        return method.isAnnotationPresent(GetMapping.class)
                || method.isAnnotationPresent(PutMapping.class)
                || method.isAnnotationPresent(DeleteMapping.class);
    }

    private Optional<Method> findMethodByOperationId(String operationId) {
        Set<Class<?>> candidates = scanClassesInPackage(basePackageToScan);

        for (Class<?> clazz : candidates) {
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(Operation.class)) {
                    String opId = method.getAnnotation(Operation.class).operationId();
                    if (opId.equals(operationId)) {
                        return Optional.of(method);
                    }
                }
            }
        }
        return Optional.empty();
    }

    private Set<Class<?>> scanClassesInPackage(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> allTypes = new HashSet<>();
        allTypes.addAll(reflections.getTypesAnnotatedWith(org.springframework.web.bind.annotation.RestController.class));
        allTypes.addAll(reflections.getTypesAnnotatedWith(org.springframework.web.bind.annotation.RequestMapping.class));
        return allTypes;
    }

    private void addIfMissing(ApiResponses responses, String code, String description) {
        if (!responses.containsKey(code)) {
            responses.addApiResponse(code, createResponse(description));
        }
    }

    private ApiResponse createResponse(String description) {
        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(createInlineSchema())));
    }

    @SuppressWarnings("unchecked")
    private Schema<?> createInlineSchema() {
        Schema<?> schema = new Schema<>();
        schema.setType("object");

        Map<String, Schema<?>> properties = new LinkedHashMap<>();
        properties.put("code", new StringSchema().example("E001"));
        properties.put("message", new StringSchema().example("Message error description."));
        properties.put("details", new ArraySchema().items(new StringSchema()));
        properties.put("timeStamp", new StringSchema().example("2024-05-01T20:35:10"));

        schema.setProperties((Map) properties);
        return schema;
    }
}
