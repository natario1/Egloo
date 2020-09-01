package com.otaliastudios.opengl.core

public interface GlBindable {
    public fun bind()
    public fun unbind()
}

public fun GlBindable.use(block: () -> Unit) {
    bind()
    block()
    unbind()
}

public fun use(vararg bindables: GlBindable, block: () -> Unit) {
    bindables.forEach { it.bind() }
    block()
    bindables.forEach { it.unbind() }
}