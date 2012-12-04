package es.osoco.grails.plugins.extraval.test

@Category(GroovyObject)
class ValidationAssertions {
    void hasError(args, o) {
        assert args.onField in o.errors.allErrors*.field
        assert args.error in o.errors.allErrors.find { it.field == args.onField }.codes
    }

    void hasNoError(args, o) {
        assert !(args.onField in o.errors.allErrors*.field)
    }
}
