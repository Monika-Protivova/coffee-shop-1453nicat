package com.motycka.edu.error

class UnauthorizedException(message: String? = null) : RuntimeException(message)
class BadRequestException(message: String = "Bad Request") : RuntimeException(message)
class NotFoundException(message: String = "Not Found") : RuntimeException(message)