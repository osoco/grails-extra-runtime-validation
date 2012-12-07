# Summary
Allows performing additional validations on Grails domain objects at runtime. Adds an overloaded ``validate(Closure extraConstraints)`` method to every domain class.

# Installation
Include the plugin in ``BuildConfig.groovy``:

```groovy
plugins {
    compile ":extra-runtime-validation:0.1"
}
```
or if you are somehow old school and like deprecated features:

```
grails install-plugin extra-runtime-validation
```

# Usage

## Typical scenario

### Given 
a domain class ``Author`` with a nullable ``firstName`` field:

```groovy
class Author {
    String firstName
    String lastName

    static constraints = {
        firstName nullable: true
        lastName blank: false
    }
}
```

### When
we want to validate that the ``firstName`` is neither null nor blank (as required by a specific use case).

### Then

validate the domain object with the standard ``validate`` method:

```groovy
anAuthor.validate()
```

#### And
perform some additional validations with extra constraints:

```groovy
anAuthor.validate {
	firstName nullable: false, blank: false
}
```

#### And
do something with validation errors.

# Unit testing

The plugin doesn't provide a direct support for mocking the ``validate(Closure)``. The reason behind this is that the plugin should remain compatible with both Grails 1.3 and 2.x (which implement mocking in a different way). 

However, the plugin includes a helper method ``ValidationUtils.addValidateWithExtraConstraints(Object, ApplicationContext)`` which adds the new validation method to a concrete **instance**. Dependending on the Grails version and assuming you are using Spock, you mock ``validate(Closure)`` in the following way:

### Grails 1.3
```
import es.osoco.grails.plugins.extraval.ValidationUtils
import org.codehaus.groovy.grails.support.MockApplicationContext

mockDomain(Author)
def o = new Author(...)
// With JUnit and a subclass of GrailsUnitTestCase, you don't have to create the MockApplicationContext. Take it from the parent class (applicationContext property)
def ctx = new MockApplicationContext()
ctx.registerMockBean('messageSource', null)
ValidationUtils.addValidateWithExtraConstraints(o, ctx)
```

### Grails 2.x
```
import es.osoco.grails.plugins.extraval.ValidationUtils

@TestMixin(DomainClassUnitTestMixin)

mockDomain(Author)
def o = new Author(...)
ValidationUtils.addValidateWithExtraConstraints(o, applicationContext)
```

## Final notes

* ``validate(Closure)`` must be called after the standard ``validate`` method so that additional errors are retained. If called before, the built-in validation cleans the error list.
* Validation of extra constraints adds errors and doesn't replace them.
* Extra constraints neither replace nor modify intrinsic constraints