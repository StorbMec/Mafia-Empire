@file:Suppress("SpellCheckingInspection")

package dev.gangster.game.data

/*
  Format: itemtype_subtype_id_level

  some unknown item:
    item_category_4_19_new: N27 LAW
    item_category_4_31_new: Ironfist 4
    item_category_4_40_new: Micro USB
  ignored:
    items_old
*/

data class ItemRes(
    val subtypeId: Int,
    val itemId: Int,

    /**
     * Depending on specific item:
     * - for most item it's the rarity (i.e., red glow, purple glow)
     * - for accessory it's the level
     */
    val level: Int,
    val name: String,
)

@Suppress("unused")
object ItemSubtypeId {
    const val ACCESSORY = 0
    const val CONSUMABLE = 0
    const val EXTRAS = 1
    const val FOOD = 0
    const val SKILL = 0

    const val GEAR_VEHICLE = 1
    const val GEAR_HOUSE = 2
    const val GEAR_PET = 3
    const val GEAR_LUXURY = 4
    const val GEAR_BODYGUARD = 5
    const val GEAR_EQUIPMENT = 6
    const val GEAR_TATTOO = 7
    const val GEAR_LUCKY_CHARM = 8

    const val WEAPON_PISTOL = 1
    const val WEAPON_SMG = 2
    const val WEAPON_SHOTGUN = 3
    const val WEAPON_ASSAULT = 4
    const val WEAPON_SNIPER = 5
}


// some items are duplicate because of rarity (requirement of client side for language)
// on server side, rarity can be altered via level in PBItem
@Suppress("unused")
object ItemIds {
    const val ACCESSORY_BULLETPROOF_VEST = 1
    const val ACCESSORY_RIOT_SHIELD = 2
    const val ACCESSORY_RED_DOT_SIGHT = 3
    const val ACCESSORY_HOLOGRAPHIC_VISOR = 4
    const val ACCESSORY_TACTICAL_TELESCOPIC_SIGHT = 5
    const val ACCESSORY_THERMAL_IMAGE_TELESCOPIC_SIGHT = 6
    const val ACCESSORY_HEARTBEAT_SENSOR = 7
    const val ACCESSORY_CAMOUFLAGE_SUIT = 8

    const val CONSUMABLE_FULL_METAL_JACKET_BULLET_XXL_SMALL_CALIBER = 1
    const val CONSUMABLE_FULL_METAL_JACKET_BULLET_XXL_SHOTGUN = 2
    const val CONSUMABLE_FULL_METAL_JACKET_BULLET_XXL_LARGE_CALIBER = 3
    const val CONSUMABLE_TRACER_AMMUNITION_XXL_SMALL_CALIBER = 4
    const val CONSUMABLE_TRACER_AMMUNITION_XXL_SHOTGUN = 5
    const val CONSUMABLE_TRACER_AMMUNITION_XXL_LARGE_CALIBER = 6
    const val CONSUMABLE_SOFT_NOSED_BULLET_XXL_SMALL_CALIBER = 7
    const val CONSUMABLE_SOFT_NOSED_BULLET_XXL_SHOTGUN = 8
    const val CONSUMABLE_SOFT_NOSED_BULLET_XXL_LARGE_CALIBER = 9
    const val CONSUMABLE_DYNAMITE_XXL = 10
    const val CONSUMABLE_THROWING_KNIFE_XXL = 11
    const val CONSUMABLE_STUN_GRENADE_XXL = 12
    const val CONSUMABLE_SMOKE_GRENADE_XXL = 13
    const val CONSUMABLE_HEADACHE_PILL_XXL = 14
    const val CONSUMABLE_FIRST_AID_KIT_XXL = 15
    const val CONSUMABLE_BLOOD_BAG_XXL = 16
    const val CONSUMABLE_DEFIBRILLATOR_XXL = 17
    const val CONSUMABLE_WOUND_DRESSING_XXL = 18
    const val CONSUMABLE_ADRENALIN_XXL = 19
//    const val CONSUMABLE_DYNAMITE_XXL = 20

    const val EXTRAS_MYSTERY_BOX = 1

    const val FOOD_PASTA = 1
    const val FOOD_COFFEE = 2
    const val FOOD_CARROT = 3
    const val FOOD_MULTIVITAMIN_JUICE = 4
    const val FOOD_ENERGY_DRINK = 5
    const val FOOD_PASTA_XXL = 6
    const val FOOD_COFFEE_XXL = 7
    const val FOOD_CARROT_XXL = 8
    const val FOOD_MULTIVITAMIN_JUICE_XXL = 9
    const val FOOD_POWER_BAR = 10

    const val SKILL_PENETRATING_POWER = 1
    const val SKILL_MEDIC = 2
    const val SKILL_CAMOUFLAGE = 3
    const val SKILL_VIGILANCE = 4
    const val SKILL_FAST_REACTION = 5
    const val SKILL_RAPID_FIRE = 6
    const val SKILL_AKIMBO_PISTOL = 7
    const val SKILL_AKIMBO_MACHINE_GUN = 8
    const val SKILL_ACCURACY_ALL_WEAPONS = 9
    const val SKILL_ACCURACY_PISTOL = 10
    const val SKILL_ACCURACY_MACHINE_GUN = 11
    const val SKILL_ACCURACY_SHOTGUN = 12
    const val SKILL_ACCURACY_ASSAULT_RIFLE = 13
    const val SKILL_ACCURACY_SNIPER_RIFLE = 14
    const val SKILL_SPECIALIZATION_PISTOL = 15
    const val SKILL_SPECIALIZATION_MACHINE_GUN = 16
    const val SKILL_SPECIALIZATION_SHOTGUN = 17
    const val SKILL_SPECIALIZATION_ASSAULT_RIFLE = 18
    const val SKILL_SPECIALIZATION_SNIPER_RIFLE = 19

    const val GEAR_FESPA = 1
    const val GEAR_HIPPIE_VAN = 2
    const val GEAR_DAW_IWETTA = 3
    const val GEAR_FITZ_350 = 4
    const val GEAR_SCOOTER = 5
    const val GEAR_VAT = 6
    const val GEAR_BOBBY_S_BM = 7
    const val GEAR_DUOMOG = 8
    const val GEAR_AUSTIN_MASTIN_PAGODA = 9
    const val GEAR_TYPE_V = 10
    const val GEAR_AMBULANCE = 11
    const val GEAR_POLICE_CAR = 12
    const val GEAR_PANTHER_S_TYPE = 13
    const val GEAR_AUSTIN_MASTIN_CB6 = 14
    const val GEAR_ROYAL_ROYCE_GHOST_2 = 15
    const val GEAR_TRUCK = 16
    const val GEAR_FIRE_TRUCK = 17
    const val GEAR_JETPACK = 18
    const val GEAR_BUMBLE_BEE_1 = 19
    const val GEAR_VARD_PALAMINO_67 = 20
    const val GEAR_LOMBARGI_COUCH = 21
    const val GEAR_SWAT_VAN = 22
    const val GEAR_MERCADO_BONZ_Z_CLASS = 23
    const val GEAR_FEIEREI_008 = 24
    const val GEAR_HALEY_MICHALSON_C_ROD = 25
    const val GEAR_AUSTIN_MASTIN_VANCISH = 26
    const val GEAR_HABAYUSA = 27
    const val GEAR_HUMMEL_3 = 28
    const val GEAR_VARD_PALAMINO_PS500 = 29
    const val GEAR_HUGE_499 = 30
    const val GEAR_LOMBARGI_MURGELARGO = 31
    const val GEAR_PWNSCHE_3DT = 32
    const val GEAR_MERCADO_BONZ_LSL_OMG = 33
    const val GEAR_APATCHI_HA_46 = 34
    const val GEAR_BAGOTTI_VENOM = 35
    const val GEAR_LITTLE_NELLIE = 36
    const val GEAR_APRILBACH_EXODUS = 37
    const val GEAR_CARDBOARD_BOX = 38
    const val GEAR_SHACK = 39
    const val GEAR_DOSSHOUSE = 40
    const val GEAR_SMALL_HOUSE = 41
    const val GEAR_TOWNHOUSE = 42
    const val GEAR_BUNGALOW = 43

    //    const val GEAR_BUNGALOW = 44
//    const val GEAR_BUNGALOW = 45
    const val GEAR_BIG_HOUSE = 46

    //    const val GEAR_BIG_HOUSE = 47
//    const val GEAR_BIG_HOUSE = 48
//    const val GEAR_BIG_HOUSE = 49
    const val GEAR_VILLA = 50

    //    const val GEAR_VILLA = 51
//    const val GEAR_VILLA = 52
//    const val GEAR_VILLA = 53
//    const val GEAR_VILLA = 54
//    const val GEAR_VILLA = 55
    const val GEAR_ORANGERY = 56
    const val GEAR_IMPRESSIVE_WHITE_HOUSE = 57
    const val GEAR_POMPOUS_CASTLE = 58
    const val GEAR_COCKROACH = 59
    const val GEAR_MOUSE = 60
    const val GEAR_GOLDFISH = 61
    const val GEAR_RAT = 62
    const val GEAR_BUNNY = 63
    const val GEAR_CAT = 64
    const val GEAR_SPIDER = 65
    const val GEAR_SNAKE = 66
    const val GEAR_BUDGIE = 67
    const val GEAR_CHIHUAHUA = 68
    const val GEAR_PIRANHA = 69
    const val GEAR_BOXER = 70
    const val GEAR_GREAT_DANE = 71
    const val GEAR_FALCON = 72
    const val GEAR_DOBERMAN = 73
    const val GEAR_ROTTWEILER = 74
    const val GEAR_PITBULL = 75
    const val GEAR_GERMAN_SHEPHERD = 76
    const val GEAR_BULL_TERRIER = 77
    const val GEAR_TIGER = 78
    const val GEAR_MOBILE_PHONE = 79

    //    const val GEAR_MOBILE_PHONE = 80
    const val GEAR_WATCH = 81
    const val GEAR_SUNGLASSES = 82
    const val GEAR_MP3_PLAYER = 83
    const val GEAR_CIGAR = 84
    const val GEAR_MONEY_BATCH = 85
    const val GEAR_RING = 86
    const val GEAR_CHAMPAGNE = 87
    const val GEAR_COCKTAIL = 88
    const val GEAR_LAPTOP = 89
    const val GEAR_SILK_SCARF = 90
    const val GEAR_DOLEX = 91
    const val GEAR_OAK_WINE_BARREL = 92
    const val GEAR_PLASMA_TV = 93
    const val GEAR_SMARTPHONE = 94
    const val GEAR_ANTIQUE_GLOBE = 95
    const val GEAR_CHANDELIER = 96
    const val GEAR_PORTRAIT_OF_DR_GACHET = 97
    const val GEAR_THE_PERSISTENCY_OF_MEMORY = 98
    const val GEAR_AZTEC_MASK = 99
    const val GEAR_THE_SCREAM = 100
    const val GEAR_CRYSTAL_SKULL = 101
    const val GEAR_MONA_LISA = 102
    const val GEAR_TRAMP = 103
    const val GEAR_ROWDY = 104
    const val GEAR_AMATEUR_BOXER = 105
    const val GEAR_CROOK = 106
    const val GEAR_PROFESSIONAL_BULLY = 107
    const val GEAR_WRESTLER = 108
    const val GEAR_MUAY_THAI_BOXER = 109
    const val GEAR_DOGGY_WALKER = 110
    const val GEAR_BOWMAN = 111
    const val GEAR_ASSASSIN = 112
    const val GEAR_SPEZNAS = 113
    const val GEAR_FAMILY_MEMBER = 114
    const val GEAR_LOYAL_POLITICIAN = 115
    const val GEAR_CUSTOMS_OFFICER = 116
    const val GEAR_CORRUPT_POLICE_OFFICER = 117
    const val GEAR_US_MARINE = 118
    const val GEAR_SHAOLIN = 119
    const val GEAR_CORRUPT_SWAT_OFFICER = 120
    const val GEAR_SNIPER = 121
    const val GEAR_FEDERAL_PROSECUTOR = 122
    const val GEAR_ATTORNEYS = 123
    const val GEAR_HITMAN = 124
    const val GEAR_JUDGE = 125
    const val GEAR_SECRET_AGENT = 126
    const val GEAR_FINGERLESS_LEATHER_GLOVES = 127
    const val GEAR_MORION = 128
    const val GEAR_WELLINGTONS = 129
    const val GEAR_ROPE = 130
    const val GEAR_SCOTCH_TAPE = 131
    const val GEAR_SCOOTER_HELMET = 132
    const val GEAR_WELDING_GOGGLES = 133
    const val GEAR_FLASHLIGHT = 134
    const val GEAR_BINOCULARS = 135
    const val GEAR_BLACK_BOOTS = 136
    const val GEAR_STURDY_LEATHER_GLOVES = 137
    const val GEAR_WIRE_CUTTERS = 138
    const val GEAR_GAS_MASK = 139
    const val GEAR_RIOT_HELMET = 140
    const val GEAR_FAKE_LICENSE_PLATES = 141
    const val GEAR_STEEL_CAPPED_BOOTS = 142
    const val GEAR_NIGHTVISION_GOGGLES = 143
    const val GEAR_SET_OF_SKELETON_KEYS = 144
    const val GEAR_BIKER_MASK = 145
    const val GEAR_GRAPPLING_HOOK = 146
    const val GEAR_EVIL_CLOWN_MASK = 147
    const val GEAR_SCREAM_MASK = 148
    const val GEAR_CONCRETE_BOOTS = 149
    const val GEAR_STAR = 150
    const val GEAR_ROSE = 151
    const val GEAR_ANCHOR = 152
    const val GEAR_SKULL = 153
    const val GEAR_BLOOD = 154
    const val GEAR_BUTTERFLY = 155
    const val GEAR_SPIDERWEB = 156
    const val GEAR_GUNS = 157
    const val GEAR_ATOMIC = 158
    const val GEAR_TRIBAL = 159
    const val GEAR_BARBED_WIRE = 160
    const val GEAR_SCORPION = 161
    const val GEAR_BIOHAZARD = 162
    const val GEAR_ANKH = 163

    //    const val GEAR_TRIBAL = 164
    const val GEAR_DRAGON = 165
    const val GEAR_WOLF = 166
    const val GEAR_ACE_OF_SPADES = 167
    const val GEAR_HATE = 168
    const val GEAR_LOVE = 169
    const val GEAR_MOM = 170
    const val GEAR_FLAMING_SKULL = 171
    const val GEAR_BARCODE = 172
    const val GEAR_LUCKY_PENNY = 173
    const val GEAR_LADYBUG = 174
    const val GEAR_FOUR_LEAF_CLOVER = 175
    const val GEAR_FLY_AGARIC = 176
    const val GEAR_LUCKY_PIG = 177
    const val GEAR_HORSESHOE = 178
    const val GEAR_MANEKI_NEKO = 179
    const val GEAR_WIPPO_LIGHTER = 180
    const val GEAR_DOG_TAG = 181
    const val GEAR_DICE = 182
    const val GEAR_RAZOR_BLADE = 183
    const val GEAR_RABBIT_S_FOOT = 184
    const val GEAR_DARUMA_DOLL = 185
    const val GEAR_FOUR_ACES = 186
    const val GEAR_8_BALL = 187
    const val GEAR_BLOODY_MOP = 188
    const val GEAR_HORSE_S_HEAD = 189
    const val GEAR_GIOVANNI_S_LOMBARGI = 190
    const val GEAR_DON_S_NCHEZ_VARD_VICTORY = 191
    const val GEAR_POLICE_UNIFORM = 192
    const val GEAR_MARIO_S_AUSTIN_MASTIN = 193

    const val WEAPON_LIAR = 1
    const val WEAPON_1911 = 2
    const val WEAPON_S30 = 3
    const val WEAPON_USB = 4
    const val WEAPON_LUCK_17 = 5
    const val WEAPON_MODEL_500 = 6
    const val WEAPON_SNOW_EAGLE = 7
    const val WEAPON_GOLDEN_SNOW_EAGLE = 8
    const val WEAPON_OZZY = 9
    const val WEAPON_MP6 = 10
    const val WEAPON_BOBBY_GUN = 11
    const val WEAPON_FM_P80 = 12
    const val WEAPON_DTI_VIKTOR = 13
    const val WEAPON_UMB = 14
    const val WEAPON_BZ_SKORPID = 15
    const val WEAPON_WALDO_MP = 16
    const val WEAPON_MP8 = 17
    const val WEAPON_SAWN_OFF_SHOTGUN = 18
    const val WEAPON_R780 = 19
    const val WEAPON_FUN_12 = 20
    const val WEAPON_PANCAKE_HAMMERJACK = 21
    const val WEAPON_NTS244 = 22
    const val WEAPON_WINGCHESTER_1887 = 23
    const val WEAPON_GREENING_A_5 = 24
    const val WEAPON_RPG_94 = 25
    const val WEAPON_CAPO_VITO_S_SHOT_GUN = 26
    const val WEAPON_BUTCHER_S_SHOTGUN = 27
    const val WEAPON_M17 = 28
    const val WEAPON_AK_24_7 = 29
    const val WEAPON_M5A2 = 30
    const val WEAPON_SICK_440 = 31
    const val WEAPON_SATYR_GAU = 32
    const val WEAPON_M70 = 33
    const val WEAPON_G63 = 34
    const val WEAPON_FARMAS = 35
    const val WEAPON_TREVOR_DAR_22 = 36
    const val WEAPON_BUSHWHACKER_AZR = 37
    const val WEAPON_KPB_B_92 = 38
    const val WEAPON_EDDY_FROST_S_M70 = 39
    const val WEAPON_BRAD_M83 = 40
    const val WEAPON_DRAGON_SVD = 41
    const val WEAPON_MOSID_NOUGAT = 42
    const val WEAPON_M41 = 43
    const val WEAPON_DCR_2 = 44
    const val WEAPON_REDEMPTION_RSAS = 45
    const val WEAPON_PSK1 = 46
    const val WEAPON_JAH_S_PEACEMAKER = 47
    const val WEAPON_F9000 = 48

    //    const val WEAPON_CAPO_VITO_S_SHOT_GUN = 49
//    const val WEAPON_BUTCHER_S_SHOTGUN = 50
    const val WEAPON_MINI_R780 = 51
    const val WEAPON_THE_BUTCHER_S_MACHINE_GUN = 52
}
