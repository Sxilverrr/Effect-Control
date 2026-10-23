plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.17-SNAPSHOT" apply false
    id("architectury-plugin") version "3.5.168" apply false
    id("com.gradleup.shadow") version "9.0.0" apply false
}
stonecutter active "1.20.1" /* [SC] DO NOT EDIT */

stonecutter parameters {
    val v1_21_11 = eval(metadata.version, ">=1.21.11")
    replacement(v1_21_11, "ResourceLocation", "Identifier")
    replacement(v1_21_11, ".location()", ".identifier()")
}

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("buildAndCollect")
}

for (it in stonecutter.tree.branches) {
    if (it.id.isEmpty()) continue
    val loader = it.id.upperCaseFirst()
    stonecutter registerChiseled tasks.register("chiseledBuild$loader", stonecutter.chiseled) {
        group = "project"
        versions { branch, _ -> branch == it.id }
        ofTask("buildAndCollect")
    }
}

for (it in stonecutter.tree.nodes) {
    if (it.metadata != stonecutter.current || it.branch.id.isEmpty()) continue
    val types = listOf("Client", "Server")
    val loader = it.branch.id.upperCaseFirst()
    for (type in types) tasks.register("runActive$type$loader") {
        group = "project"
        dependsOn("${it.hierarchy}:run$type")
    }
}
