package dev.gangster.game.data

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import java.util.zip.ZipFile
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Global registry that holds game data and config from parsing the game XML resource.
 *
 * The game XML resource is from decompressing en.ggs (with gzip) and producing mafia_en.mxl
 */
class GameDefinitions() {
    val accessoryById: MutableMap<Int, ItemRes> = mutableMapOf()
    val consumableById: MutableMap<Int, ItemRes> = mutableMapOf()
    val extrasById: MutableMap<Int, ItemRes> = mutableMapOf()
    val foodById: MutableMap<Int, ItemRes> = mutableMapOf()
    val skillById: MutableMap<Int, ItemRes> = mutableMapOf()
    val gearById: MutableMap<Int, ItemRes> = mutableMapOf()
    val weaponById: MutableMap<Int, ItemRes> = mutableMapOf()

    val questById: MutableMap<Int, QuestRes> = mutableMapOf()

    val enemyMaleFirstName: MutableList<String> = mutableListOf()
    val enemyFemaleFirstName: MutableList<String> = mutableListOf()
    val enemySurnames: MutableList<String> = mutableListOf()

    init {
        val path = "static/gangster-data/games-languages/4/pt.ggs"
        val file = ZipFile(File(path))
        val entry = file.getEntry("Mafia_pt.xml") ?: error("Mafia_pt.xml not found in $path")
        val doc = file.getInputStream(entry).use { input ->
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            builder.parse(input)
        }

        parseItems(doc)
        parseQuests(doc)
        parseEnemyNames(doc)
    }

    fun parseItems(doc: Document) {
        val itemsNode = doc.getElementsByTagName("items").item(0) as Element
        val categories = itemsNode.childNodes

        for (i in 0 until categories.length) {
            val node = categories.item(i)
            if (node.nodeType != Node.ELEMENT_NODE) continue
            val category = node as Element

            when (category.tagName) {
                "items_accessory" -> loopTexts(category, accessoryById)
                "items_consumable" -> loopTexts(category, consumableById)
                "items_extras" -> loopTexts(category, extrasById)
                "items_food" -> loopTexts(category, foodById)
                "items_skill" -> loopTexts(category, skillById)

                "items_weapon" -> {
                    val groups = category.childNodes
                    for (j in 0 until groups.length) {
                        val groupNode = groups.item(j)
                        if (groupNode.nodeType != Node.ELEMENT_NODE) continue
                        loopTexts(groupNode as Element, weaponById)
                    }
                }

                "items_gear" -> {
                    val groups = category.childNodes
                    for (j in 0 until groups.length) {
                        val groupNode = groups.item(j)
                        if (groupNode.nodeType != Node.ELEMENT_NODE) continue
                        loopTexts(groupNode as Element, gearById)
                    }
                }
            }
        }
    }

    private fun loopTexts(parent: Element, target: MutableMap<Int, ItemRes>) {
        val children = parent.childNodes
        for (i in 0 until children.length) {
            val node = children.item(i)
            if (node.nodeType != Node.ELEMENT_NODE) continue
            val el = node as Element
            if (el.tagName == "text") {
                parseItem(el)?.let { item -> target[item.itemId] = item }
            }
        }
    }

    private fun parseItem(el: Element): ItemRes? {
        val id = el.getAttribute("id") // e.g. accessory_0_1_1
        val name = el.getAttribute("name")

        val parts = id.split("_")
        if (parts.size < 4) return null
        val subtypeId = parts[1].toInt()
        val itemId = parts[2].toInt()
        val level = parts[3].toInt()

        return ItemRes(subtypeId, itemId, level, name)
    }

    private fun parseQuests(doc: Document) {
        val questsNode = doc.getElementsByTagName("quests").item(0) as? Element
        questsNode?.let {
            val tasksGroups = it.getElementsByTagName("tasks")
            for (i in 0 until tasksGroups.length) {
                val groupNode = tasksGroups.item(i)
                if (groupNode.nodeType != Node.ELEMENT_NODE) continue
                val groupEl = groupNode as Element

                val textNodes = groupEl.getElementsByTagName("text")
                for (j in 0 until textNodes.length) {
                    val textNode = textNodes.item(j)
                    if (textNode.nodeType != Node.ELEMENT_NODE) continue
                    val textEl = textNode as Element

                    parseQuest(textEl)?.let { q ->
                        questById[q.questId] = q
                    }
                }
            }
        }
    }

    private fun parseQuest(el: Element): QuestRes? {
        val id = el.getAttribute("id") // e.g. MafiaQuest_task_1
        val name = el.getAttribute("name")

        val parts = id.split("_")
        if (parts.size < 3) return null
        val questId = parts[2]

        return QuestRes(questId.toInt(), name)
    }

    fun parseEnemyNames(doc: Document) {
        val enemyNamesNode = doc.getElementsByTagName("enemynames").item(0) as? Element
        enemyNamesNode?.childNodes?.let { nodes ->
            for (i in 0 until nodes.length) {
                val node = nodes.item(i)
                if (node.nodeType != Node.ELEMENT_NODE) continue
                val enemyName = node as Element
                if (enemyName.tagName != "text") continue

                val id = enemyName.getAttribute("id") // e.g., MafiaEnemyName_female_1
                val name = enemyName.getAttribute("name")

                when {
                    id.startsWith("MafiaEnemyName_female") -> enemyMaleFirstName.add(name)
                    id.startsWith("MafiaEnemyName_male") -> enemyFemaleFirstName.add(name)
                    id.startsWith("MafiaEnemyName_surname") -> enemySurnames.add(name)
                }
            }
        }
    }
}
