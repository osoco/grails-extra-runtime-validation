import org.codehaus.groovy.grails.plugins.DomainClassPluginSupport
import org.codehaus.groovy.grails.validation.ConstrainedPropertyBuilder

class ExtraRuntimeValidationGrailsPlugin {
    def version = "0.0.1"
    def grailsVersion = "1.3.7 > *"
    def dependsOn = [:]
    def title = "Extra runtime validation for domain objects"
    def organization = [name: "OSOCO", url: "http://osoco.es/"]
    def developers = [
        [name: "Marcin Gryszko", email: "marcin.gryszko@osoco.es"],
        [name: "David Molinero", email: "david.molinero@osoco.es"]]

    def pluginExcludes = [
        "grails-app/domain/**/*"
    ]
    def description = 'Adds validate(Closure extraConstraints) method to domain objects to perform additional validations at runtime.'

    def license = "APACHE"
    def documentation = "https://github.com/osoco/grails-extra-runtime-validation"
    def scm = [url: "https://github.com/osoco/grails-extra-runtime-validation"]
    def issueManagement = [system: "GitHub", url: "https://github.com/osoco/grails-extra-runtime-validation/issues"]

    def doWithDynamicMethods = { ctx ->
        application.domainClasses.each { domainClass ->
            addValidationWithExtraConstraints(domainClass, ctx)
        }
    }

    private addValidationWithExtraConstraints(domainClass, ctx) {
        domainClass.metaClass.validate = { Closure extraConstraints ->
            def extraConstrainedProps = buildExtraConstrainedProperties(domainClass, extraConstraints)
            def intrinsicConstraints = delegate.constraints
            delegate.metaClass.getConstraints = {-> extraConstrainedProps }
            def valid = DomainClassPluginSupport.validateInstance(delegate, ctx)
            delegate.metaClass.getConstraints = {-> intrinsicConstraints }
            valid
        }
    }

    private buildExtraConstrainedProperties(domainClass, extraConstraints) {
        def constrainedPropertyBuilder = new ConstrainedPropertyBuilder(domainClass.clazz)
        extraConstraints.delegate = constrainedPropertyBuilder
        extraConstraints()
        constrainedPropertyBuilder.constrainedProperties
    }
}
