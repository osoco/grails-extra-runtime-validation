package es.osoco.grails.plugins.extraval
import grails.plugin.spock.IntegrationSpec

class ExtraConstraintsSpec extends IntegrationSpec {
    private validSample

    def setup() {
        validSample = new Author(lastName: 'Asimov')
    }

    def 'valid sample validates successfully'() {
        expect:
        validSample.validate()
    }

    def 'intrinsic constraints are validated as before'() {
        given:
        validSample.lastName = ' \r\n\t '

        expect:
        !validSample.validate()
        hasError validSample, onField: 'lastName', error: 'blank'
    }

    def 'extra constraints can be passed to the validation method as closure'() {
        given:
        validSample.firstName = null

        expect:
        !validSample.validate({ firstName nullable: false })
        hasError validSample, onField: 'firstName', error: 'nullable'
    }

    def "validation of extra constraints called after standard validation adds errors and doesn't replace them"() {
        given:
        validSample.firstName = null
        validSample.lastName = ' \r\n\t '

        when:
        validSample.validate()

        then:
        hasError validSample, onField: 'lastName', error: 'blank'
        hasNoError validSample, onField: 'firstName'

        when:
        validSample.validate({ firstName nullable: false })

        then:
        hasError validSample, onField: 'lastName', error: 'blank'
        hasError validSample, onField: 'firstName', error: 'nullable'
    }

    def "extra constraints don't replace intrinsic constraints"() {
        given:
        validSample.firstName = null
        validSample.lastName = ' \r\n\t '

        when:
        validSample.validate({ firstName nullable: false })
        validSample.validate()

        then:
        hasError validSample, onField: 'lastName', error: 'blank'
        hasNoError validSample, onField: 'firstName'
    }

    def "after validation of extra constraints, class intrinsic constraints are not modified"() {
        given:
        hasSingleNullableTrueConstraint Author.constraints.firstName
        hasSingleNullableTrueConstraint validSample.constraints.firstName

        when:
        validSample.firstName = null
        validSample.validate({ firstName nullable: false })

        then:
        hasSingleNullableTrueConstraint Author.constraints.firstName
        hasSingleNullableTrueConstraint validSample.constraints.firstName
    }

    private void hasError(args, obj) {
        assert args.onField in obj.errors.allErrors*.field
        assert args.error in obj.errors.allErrors.find { it.field == args.onField }.codes
    }

    private void hasNoError(args, obj) {
        assert !(args.onField in obj.errors.allErrors*.field)
    }

    private void hasSingleNullableTrueConstraint(constrainedProperty) {
        assert constrainedProperty.appliedConstraints.size() == 1
        assert constrainedProperty.appliedConstraints.toList().first().nullable
    }
}
