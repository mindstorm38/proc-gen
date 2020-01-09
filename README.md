# ProcGen

This project is a Minecraft clone, I originally created this project
in order to understand procedural generation and I thought creating
a MC clone was the best easy way to visualize this type of
generation.

## Play (version 0.1.0)

- Escape to switch pause mode
- Use mouse to move the camera
- Press `L` to generate terrain around the camera
- Press `I` to instantiate a falling block entity
- Use `ZQSD` (or WASD) to move
- Space and Shift to move verticaly
- Use `F11` to toggle fullscreen

> If you want to customize controls, you can edit the `options.json` next to downloaded jar file.
> You can use the [GLFW page about keys ids](https://www.glfw.org/docs/latest/group__keys.html) to know numbers to put in the configuration file to change control keys.
> This config file will be supported in future version.

## This content is coming

- [x] Motion entities
- [x] Pigs
  - [x] Model
  - [ ] Animations
  - [ ] AI
- [ ] Asynchronous (multi-threaded) chunk generation
- [ ] Asynchronous world chunk saving on file system
- [ ] Items
- [ ] All-model falling blocks
- [ ] TNT

## Contribute

This game is currently in development, it is not playable but it is
already possible to move through the world.

For development I'm using Gradle (use wrapper with `./gradrew` and `./gradlew.bat`) and IntelliJ IDEA as IDE.