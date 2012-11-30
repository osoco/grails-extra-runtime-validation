import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.plugins.DomainClassPluginSupport
import org.codehaus.groovy.grails.validation.ConstrainedPropertyBuilder
import org.springframework.beans.BeanUtils
import org.springframework.context.ApplicationContext
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors

class ExtraRuntimeValidationGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.7 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def author = "Your name"
    def authorEmail = ""
    def title = "Plugin summary/headline"
    def description = '''\\
Brief description of the plugin.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/extra-runtime-validation"

    def doWithDynamicMethods = { ctx ->
        application.domainClasses.each { domainClass ->
            addValidationWithExtraConstraints(domainClass, ctx)
        }
    }

    private addValidationWithExtraConstraints(domainClass, ctx) {
        domainClass.metaClass.validate = { Closure extraConstraints ->
            def extraConstrainedProps = buildExtraConstrainedProperties(domainClass, extraConstraints)
            def intrinsicConstraints = delegate.constraints
            delegate.metaClass.getConstraints = { -> extraConstrainedProps }
            def valid = DomainClassPluginSupport.validateInstance(delegate, ctx)
            delegate.metaClass.getConstraints = { -> intrinsicConstraints }
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
