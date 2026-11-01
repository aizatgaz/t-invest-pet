window.addEventListener("load", function () {
    window.ui = SwaggerUIBundle({
        url: "../api-docs/openapi.yaml",
        dom_id: "#swagger-ui",
        deepLinking: true,
        validatorUrl: null,
        persistAuthorization: false,
        presets: [SwaggerUIBundle.presets.apis],
        layout: "BaseLayout"
    });
});
