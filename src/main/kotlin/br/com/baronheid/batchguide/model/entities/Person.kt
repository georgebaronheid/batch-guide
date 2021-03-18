package br.com.baronheid.batchguide.model.entities

data class Person(var firstName: String, var lastName: String) {
    constructor() : this(
        "",
        ""
    )
}
