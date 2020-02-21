package pl.starchasers.mdpages

import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.MethodOrdererContext

class AnnotationMethodOrderer : MethodOrderer {
    override fun orderMethods(context: MethodOrdererContext) {
        context.methodDescriptors.sortBy { method -> if (method.isAnnotated(DocumentResponse::class.java)) 1 else 0 }
    }
}