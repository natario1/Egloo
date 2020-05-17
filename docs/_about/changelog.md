---
layout: page
title: "Changelog"
order: 3
---

New versions are released through GitHub, so the reference page is the [GitHub Releases](https://github.com/natario1/Egloo/releases) page.

Starting from v0.3.1, you can [support development](https://github.com/sponsors/natario1) through the GitHub Sponsors program. 
Companies can share a tiny part of their revenue and get private support hours in return. Thanks!

### v0.5.0

This release adds support for native targets. We provide an implementation for Android native libraries,
but other targets like iOS can probably be added easily. These artifacts are not currently published
but can be built using `./gradlew :library:publishLocal` . ([#20][20]).

Other changes:

- New: `EglCore.makeCurrent()` to make the context current with no surfaces ([#18][18])
- New: `GlBuffer` base class, and `GlShaderStorageBuffer` implementation for SSBOs
- New: `GlShader` abstraction for `GlProgram`s

<https://github.com/natario1/Egloo/compare/v0.4.0...v0.5.0>

### v0.4.0

- New: `GlTexture` class to create textures ([#14][14])
- New: `GlFramebuffer` class to create framebuffers ([#14][14])
- New: `Gl2dMesh` drawable ([#14][14])

<https://github.com/natario1/Egloo/compare/v0.3.1...v0.4.0>

### v0.3.1

First versioned release.

[natario1]: https://github.com/natario1

[14]: https://github.com/natario1/Egloo/pull/14
[18]: https://github.com/natario1/Egloo/pull/18
[20]: https://github.com/natario1/Egloo/pull/20
