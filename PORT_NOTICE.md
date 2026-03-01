# Port Notice — NBT Editor (Minecraft 1.21.11)

Thank you for checking out this quick port of NBT Editor for Minecraft 1.21.11.

**Important build notes:**

- **Recommended build:** run `./gradlew build` — this will build the mod successfully.
- **Run client:** `./gradlew runClient` may fail because of mixin-related issues; in my testing the mixins often prevent a successful dev-client launch.
- There are some warnings and minor compilation errors in the source. I uploaded the port quickly so you can use or inspect it — these warnings do not prevent the build when using `./gradlew build` but may cause `runClient` to fail.

**About this port**

- I originally ported this for my own use and uploaded it quickly to share it with others who might find it useful.
- I'm not a professional developer; the code may contain rough edges or small mistakes. If you find issues or want to improve anything, contributions and fixes are welcome.

**If you plan to use the source**

- You are free to use and modify this code. Be aware of the warnings and potential mixin issues described above.
- If the warnings bother you, please feel free to clean them up or submit a PR — I'm happy to accept improvements.

Thanks for checking the project, and have a great game!

---

Спасибо, что посмотрели порт NBT Editor для Minecraft 1.21.11.

Коротко по-русски:

- Для компиляции используйте `./gradlew build` — сборка проходит успешно.
- `./gradlew runClient` скорее всего не запустится из-за проблем с mixin-микшинами.
- В коде есть предупреждения/небольшие ошибки — я загрузил проект быстро, чтобы поделиться. Если хотите, можете исправить и прислать PR.
- Я не профессионал, поэтому в коде могут быть недочеты. Желаю приятной игры!
