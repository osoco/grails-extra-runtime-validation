package es.osoco.grails.plugins.extraval

class Author {
    String firstName
    String lastName

    static constraints = {
        firstName nullable: true
        lastName blank: false
    }
}
