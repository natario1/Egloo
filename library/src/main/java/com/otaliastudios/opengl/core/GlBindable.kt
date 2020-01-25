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