package com.otaliastudios.opengl.core

interface GlBindable {
    fun bind()
    fun unbind()
}

fun GlBindable.use(block: () -> Unit) {
    bind()
    block()
    unbind()
}

fun use(vararg bindables: GlBindable, block: () -> Unit) {
    bindables.forEach { it.bind() }
    block()
    bindables.forEach { it.unbind() }
}