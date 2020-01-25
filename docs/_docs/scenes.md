---
layout: page
title: "Scenes"
description: "How to control the view and projection matrix"
order: 5
disqus: 1
---

When drawing different drawables, using different programs or different textures that should have
common (or separate) view and projection matrices, it can be useful to use a `GlScene`.

The `GlScene` object contains and holds:

- the view matrix
- the projection matrix

The scene can combine these with each drawable's model matrix, to create the famous model-view-projection
matrix. In GLES terms, you can think of a scene as a simple matrix holder.

When using scenes, drawing should be performed through the `GlScene` itself:

```kotlin
val scene = GlScene()

// Set common view and projection matrix in the scene object.
setProjection(scene.projectionMatrix)
setView(scene.viewMatrix)

// Draw with common parameters
scene.draw(program, drawable1)
scene.draw(program, drawable2)
scene.draw(program, drawable3)
```