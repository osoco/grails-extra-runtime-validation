import es.osoco.grails.plugins.extraval.ValidationUtils

class ExtraRuntimeValidationGrailsPlugin {
    def version = "0.1"
    def grailsVersion = "1.3.7 > *"
    def title = "Extra runtime validation for domain objects"
    def organization = [name: "OSOCO", url: "http://osoco.es/"]
    def author = "OSOCO"
    def authorEmail = "info@osoco.es"
    def developers = [
        [name: "Marcin Gryszko", email: "marcin.gryszko@osoco.es"],
        [name: "David Molinero", email: "david.molinero@osoco.es"]]

    def pluginExcludes = [
        "grails-app/domain/**/*",
        "src/groovy/es/osoco/grails/plugins/extraval/test/**/*"
    ]
    def description = 'Adds validate(Closure extraConstraints) method to domain objects to perform additional validations at runtime.'

    def license = "APACHE"
    def documentation = "https://github.com/osoco/grails-extra-runtime-validation"
    def scm = [url: "https://github.com/osoco/grails-extra-runtime-validation"]
    def issueManagement = [system: "GitHub", url: "https://github.com/osoco/grails-extra-runtime-validation/issues"]

    def doWithDynamicMethods = { ctx ->
        application.domainClasses.each { domainClass ->
            ValidationUtils.addValidateWithExtraConstraints(domainClass, ctx)
        }
    }
}
