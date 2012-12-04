package es.osoco.grails.plugins.extraval

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.plugins.DomainClassPluginSupport
import org.codehaus.groovy.grails.validation.ConstrainedPropertyBuilder
import org.springframework.context.ApplicationContext


class ValidationUtils {
    static addValidateWithExtraConstraints(GrailsDomainClass domainClass, ApplicationContext ctx) {
        domainClass.metaClass.validate = createValidateWithExtraConstraintsMethod(domainClass.clazz, ctx)
    }

    static addValidateWithExtraConstraints(Object o, ApplicationContext ctx) {
        o.metaClass.validate = createValidateWithExtraConstraintsMethod(o.class, ctx)
    }

    private static createValidateWithExtraConstraintsMethod(Class clazz, ApplicationContext ctx) {
        { Closure extraConstraints ->
            def extraConstrainedProps = buildExtraConstrainedProperties(clazz, extraConstraints)
            def intrinsicConstraints = delegate.constraints
            delegate.metaClass.getConstraints = {-> extraConstrainedProps }
            def valid = DomainClassPluginSupport.validateInstance(delegate, ctx)
            delegate.metaClass.getConstraints = {-> intrinsicConstraints }
            valid
        }
    }

    private static buildExtraConstrainedProperties(Class clazz, Closure extraConstraints) {
        def constrainedPropertyBuilder = new ConstrainedPropertyBuilder(clazz)
        extraConstraints.delegate = constrainedPropertyBuilder
        extraConstraints()
        constrainedPropertyBuilder.constrainedProperties
    }
}
