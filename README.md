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

## Final notes

* ``validate(Closure)`` must be called after the standard ``validate`` method so that additional errors are retained. If called before, the built-in validation cleans the error list.
* Validation of extra constraints adds errors and doesn't replace them.
* Extra constraints neither replace nor modify intrinsic constraints