package es.osoco.grails.plugins.extraval

import es.osoco.grails.plugins.extraval.test.ValidationAssertions
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin
import spock.lang.Specification

@TestMixin(DomainClassUnitTestMixin)
class MockExtraConstraintsSpec extends Specification {
    def setup() {
        metaClass.mixin ValidationAssertions
    }

    def 'extra constraints can be mocked on an instance'() {
        given:
        mockDomain Author
        def o = new Author(lastName: 'Asimov')
        ValidationUtils.addValidateWithExtraConstraints(o, applicationContext)

        when:
        o.firstName = null
        !o.validate({ firstName nullable: false })

        then:
        hasError o, onField: 'firstName', error: 'nullable'
    }
}
