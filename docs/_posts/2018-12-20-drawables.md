---
layout: page
title: "Drawables"
description: "Egloo drawables are the shape to be drawn"
category: docs
date: 2018-12-20 20:02:08
order: 2
disqus: 1
---

In the Egloo drawing pipeline, the `GlDrawable` class controls **what to draw**.
In GLES terms, each drawable contains:
 
- its vertex array, containing the position of each vertex
- its **model matrix**, to control scale, rotation, translation, and so on
- its `glDrawArrays` logic, for example for using `GL_TRIANGLE_FAN` or `GL_TRIANGLE_STRIP`

Drawables are very easy to implement. We offer a few implementations:

|Name|Description|
|----|-----------|
|`Gl2dDrawable`|Base class for 2D drawables, that have 2 coordinates per vertex.|
|`Gl3dDrawable`|Base class for 2D drawables, that have 3 coordinates per vertex.|
|`GlRect`|A 2D drawable made of four vertices. By default, it covers the entire viewport and is typically used for textures.|
|`GlPolygon`|A regular 2D polygon. For example: `pentagon = GlPolygon(5)`.|
|`GlTriangle`|A regular 2D triangle, extending `GlPolygon`.|
|`GlSquare`|A 2D square, extending `GlPolygon`.|
|`GlCircle`|A 2D circle, implemented as a `GlPolygon` with 360 sides.|
|`GlRoundRect`|A 2D rounded rect, with customizable corners.|

Each drawable can have different methods to customize its appearance and behavior.
