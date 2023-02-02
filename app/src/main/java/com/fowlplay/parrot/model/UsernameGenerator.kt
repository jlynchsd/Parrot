package com.fowlplay.parrot.model

private val agentNouns = listOf(
    "Admirer",
    "Admonisher",
    "Associator",
    "Believer",
    "Defender",
    "Equivocator",
    "Fighter",
    "Gamer",
    "Grumbler",
    "Hunter",
    "Listener",
    "Lover",
    "Mariner",
    "Panderer",
    "Whiner"
)

private val adjectives = listOf(
    "Adorable",
    "Adventurous",
    "Angry",
    "Annoyed",
    "Average",
    "Beautiful",
    "Bewildered",
    "Bored",
    "Charming",
    "Concerned",
    "Condemned",
    "Courageous",
    "Crazy",
    "Curious",
    "Determined",
    "Embarrassed",
    "Enthusiastic",
    "Fantastic",
    "Fierce",
    "Friendly",
    "Frantic",
    "Funny",
    "Glamorous",
    "Gorgeous",
    "Grumpy",
    "Happy",
    "Lazy",
    "Magnificent",
    "Nervous",
    "Perspicacious",
    "Proud"
)

private val colors = listOf(
    "Red",
    "Orange",
    "Yellow",
    "Green",
    "Cyan",
    "Azure",
    "Blue",
    "Violet",
    "Magenta",
    "Rose",
    "Red",
    "Vermilion"
)

private val tactile = listOf(
    "Abrasive",
    "Blunt",
    "Bulky",
    "Clammy",
    "Coarse",
    "Cool",
    "Damp",
    "Dusty",
    "Etched",
    "Flat",
    "Fragile",
    "Fuzzy",
    "Grainy",
    "Greasy",
    "Grimy",
    "Harsh",
    "Itchy",
    "Lumpy",
    "Pointy",
    "Sandy",
    "Silky",
    "Tough"
)

private val animals = listOf(
    "Aardvark",
    "Bird",
    "Capybara",
    "Cat",
    "Cow",
    "Dog",
    "Dolphin",
    "Donkey",
    "Elephant",
    "Fox",
    "Giraffe",
    "Hippo",
    "Horse",
    "Iguana",
    "Jaguar",
    "Kangaroo",
    "Koala",
    "Lemming",
    "Leopard",
    "Lion",
    "Moose",
    "Panda",
    "Parrot",
    "Pig",
    "Rhino",
    "Shark",
    "Snake",
    "Sloth",
    "Squirrel",
    "Tiger",
    "Toad",
    "Toucan",
    "Walrus",
    "Whale",
    "Wolf",
    "Yak",
    "Zebra"
)

private val emojis = listOf(
    "\uD83C\uDF0A",
    "\uD83C\uDF46",
    "\uD83C\uDF51",
    "\uD83C\uDFB7",
    "\uD83C\uDFFA",
    "\uD83D\uDC0D",
    "\uD83D\uDC11",
    "\uD83D\uDC38",
    "\uD83D\uDC4A",
    "\uD83D\uDC4B",
    "\uD83D\uDC4C",
    "\uD83D\uDC50",
    "\uD83D\uDC7A",
    "\uD83D\uDC7F",
    "\uD83D\uDC80",
    "\uD83D\uDCA3",
    "\uD83D\uDCA9",
    "\uD83D\uDCAF",
    "\uD83D\uDD25",
    "\uD83D\uDE00",
    "\uD83D\uDE02",
    "\uD83D\uDE05",
    "\uD83D\uDE08",
    "\uD83D\uDE09",
    "\uD83D\uDE0D",
    "\uD83D\uDE0E",
    "\uD83D\uDE0F",
    "\uD83D\uDE18",
    "\uD83D\uDE21",
    "\uD83D\uDE29",
    "\uD83D\uDE2C",
    "\uD83D\uDE31",
    "\uD83D\uDE4F",
    "\uD83D\uDEA8"
)

private val leetLambda: (String) -> String = { input -> input
    .replace("a", "4").replace("A", "4")
    .replace("e", "3").replace("E", "3")
    .replace("o", "0").replace("O", "0")
    .replace("l", "1").replace("L", "1")
}

private val abbreviateLambda: (String) -> String = { input -> input
    .replace("a", "")
    .replace("e", "")
    .replace("i", "")
    .replace("o", "")
    .replace("u", "")
}

private val enumerateLambda: (String) -> String = { input -> input + (1 .. 70).random() }

private val mutators = listOf< (String) -> String >(
    { input -> input },
    { input -> enumerateLambda(input) },
    { input -> leetLambda(input) },
    { input -> leetLambda(input.lowercase()) },
    { input -> abbreviateLambda(input) },
    { input -> enumerateLambda(abbreviateLambda(input)) }
)

private val names = listOf(
    { adjectives.random() + agentNouns.random() },
    { adjectives.random() + animals.random() },
    { colors.random() + animals.random() },
    { tactile.random() + animals.random() },
    { tactile.random() + agentNouns.random() }
)

fun generateEmojis(min: Int, max: Int) =
    0.rangeTo((min..max).random()).joinToString(separator = "") { emojis.random() }

fun generateUsername(): String {
    return "@" + mutators.random().invoke(names.random().invoke())
}