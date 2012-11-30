package es.osoco.grails.plugins.extraval

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.plugins.DomainClassPluginSupport
import org.codehaus.groovy.grails.validation.ConstrainedPropertyBuilder

class Author {
    String firstName
    String lastName

    static constraints = {
        firstName nullable: true
        lastName blank: false
    }

    def validate(Closure extraConstraints) {
        def extraConstrainedProps = buildExtraConstrainedProperties(extraConstraints)
        def intrinsicConstraints = constraints
        metaClass.getConstraints = { -> extraConstrainedProps }
        def ctx = ApplicationHolder.application.mainContext
        def valid = DomainClassPluginSupport.validateInstance(this, ctx)
        metaClass.getConstraints = { -> intrinsicConstraints }
        valid
    }

    private buildExtraConstrainedProperties(Closure extraConstraints) {
        def constrainedPropertyBuilder = new ConstrainedPropertyBuilder(this)
        extraConstraints.setDelegate(constrainedPropertyBuilder)
        extraConstraints()
        def extraConstrainedProps = constrainedPropertyBuilder.constrainedProperties
        extraConstrainedProps
    }
}
