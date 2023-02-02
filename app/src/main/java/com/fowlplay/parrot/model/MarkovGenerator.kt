package com.fowlplay.parrot.model


private const val PREFIX_SIZE = 2
private const val SUFFIX_SIZE = 1
private const val SECTION_DELIM = "__SECTION_DELIM__"
private const val SECTION_DELIM_ROOT = "__SECTION_DELIM_ROOT__"
private const val TYPE_DELIM = "__TYPE_DELIM__"


suspend fun generateMarkovNodes(stringNodes: List<String>, parrotDatabase: ParrotDatabase) {
    val nodes = emptyList<MarkovNode>().toMutableList()
    stringNodes.forEach { stringNode ->
        if (stringNode.isNotEmpty()) {
            val rootNode = stringNode.contains(SECTION_DELIM_ROOT)
            val contents = if (rootNode) {
                stringNode.split(SECTION_DELIM_ROOT)
            } else {
                stringNode.split(SECTION_DELIM)
            }
            val prefix = contents[0]
            val suffix = contents[1].split(TYPE_DELIM).toMutableList()
            nodes.add(MarkovNode(prefix, rootNode, mutableListOf(*suffix.toTypedArray())))
        }
    }
    parrotDatabase.markovDao().insertAll(*nodes.toTypedArray())
}

suspend fun generateMarkovPhrase(
    parrotDatabase: ParrotDatabase,
    maxWordCount: Int = 25,
    minWordCount: Int = 10,
    maxAttempts: Int = 3
): String {
    var attempts = 0

    while (attempts < maxAttempts) {
        val attempt = generateMarkovPhrase(parrotDatabase, maxWordCount, minWordCount)
        if (attempt.second > 2) {
            return attempt.first
        }
        ++attempts
    }

    return generateMarkovPhrase(parrotDatabase, maxWordCount, minWordCount).first
}

private suspend fun generateMarkovPhrase(
    parrotDatabase: ParrotDatabase,
    maxWordCount: Int = 25,
    minWordCount: Int = 10
): Pair<String, Int> {
    val result = emptyList<String>().toMutableList()
    val prefixCarryOver = PREFIX_SIZE - SUFFIX_SIZE
    var markovNode = parrotDatabase.markovDao().getRandomRootNode()
    markovNode?.let {
        result += it.prefix
    }

    var branches = 0

    for (i in 0 until maxWordCount - PREFIX_SIZE) {
        markovNode?.let { node ->
            if (node.suffixes.isNotEmpty()) {
                if (node.suffixes.size > 1) {
                    branches += 1
                }
                node.suffixes.random().let { suffix ->
                    result += suffix
                    val prefix = if (prefixCarryOver > 0) {
                        node.prefix.split(" ").let {
                            it.subList(it.size - prefixCarryOver, it.size)
                                .joinToString(" ") + " $suffix"
                        }
                    } else {
                        suffix
                    }

                    result.lastOrNull()?.let {
                        if (result.size > minWordCount &&
                            it.endsWith(".") ||
                            it.endsWith("?") ||
                            it.endsWith("!")) {
                            return Pair(result.joinToString(" "), branches)
                        }
                    }

                    markovNode = parrotDatabase.markovDao().findByPrefix(prefix)
                }
            } else {
                return Pair(result.joinToString(" "), branches)
            }
        }
    }

    return Pair(result.joinToString(" "), branches)
}