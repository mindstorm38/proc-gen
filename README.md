# ProcGen

This project is a Minecraft clone, I originally created this project
in order to understand procedural generation and I thought creating
a MC clone was the best easy way to visualize this type of
generation.

> I am currently rewriting the configuration of the Gradle project using
> Kotlin DSL for MSEngine and this project. These two projects will have
> in the future a beautiful structure using gradle's subprojects for
> better managing of client and server JARs.

> For now I keep the './src' directory but it will be deleted soon.

## Play or Contribute

This game is currently in development, it is not playable but it is
already possible to move through the world.

For development I'm using Gradle (6.0) and IntelliJ IDEA as IDE.
The only dependencies are :
- [SUtil](https://github.com/mindstorm38/sutil), which is a utility library I developped.
- [MSEngine](https://gitlab.com/mindstorm38/msengine) : this is my helper over OpenGL and GLFW.

These two libraries must be placed in the same parent folder that
this repo.