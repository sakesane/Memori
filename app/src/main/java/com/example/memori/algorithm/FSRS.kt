package com.example.memori.algorithm

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.round

public enum class Status(val value: String) {
    New("New"), Learning("Learning"), Review("Review"), Relearning("Relearning");
    companion object {
        fun from(value: String): Status {
            return Status.values().find { it.value == value } ?: Status.New
        }
    }
}

enum class Rating(val description: String) {
    Again("Again"), Hard("Hard"), Good("Good"), Easy("Easy");

    // 获取评分对应的数值（1~4）
    fun value(): Int {
        return ordinal + 1
    }

    override fun toString(): String {
        return description
    }
}

class ReviewLog(
    var rating: Rating,         // 用户评分
    var elapsedDays: Double,    // 距离上次复习经过的天数
    var scheduledDays: Double,  // 计划间隔天数
    var review: LocalDateTime,  // 复习时间
    var status: Status          // 复习时卡片状态
) {
    // 转为Map，便于序列化
    fun data(): Map<String, Any> {
        return mapOf(
            "rating" to rating,
            "elapsedDays" to elapsedDays,
            "scheduledDays" to scheduledDays,
            "review" to review,
            "state" to status.value
        )
    }
}

class FSRSCard : Cloneable {

    var due: LocalDateTime = LocalDateTime.now()         // 下次复习时间
    var stability: Double = 0.0         // 稳定性（记忆保持能力）
    var difficulty: Double = 0.0        // 难度
    var elapsedDays: Double = 0.0       // 距离上次复习经过的天数
    var scheduledDays: Double = 0.0     // 计划间隔天数
    var reps: Int = 0                   // 复习次数
    var lapses: Int = 0                 // 遗忘次数
    var status: Status = Status.New     // 当前状态
    var lastReview: LocalDateTime = LocalDateTime.now()       // 上次复习时间

    constructor()

    // 克隆方法，复制卡片所有属性
    public override fun clone(): FSRSCard {
        val card = FSRSCard()
        card.due = due
        card.stability = stability
        card.difficulty = difficulty
        card.elapsedDays = elapsedDays
        card.scheduledDays = scheduledDays
        card.reps = reps
        card.lapses = lapses
        card.status = status
        card.lastReview = lastReview
        return card
    }

    // 转为Map，便于序列化
    fun data(): Map<String, Any> {
        return mapOf(
            "due" to due,
            "stability" to stability,
            "difficulty" to difficulty,
            "elapsed_days" to elapsedDays,
            "scheduled_days" to scheduledDays,
            "reps" to reps,
            "lapses" to lapses,
            "state" to status.value,
            "last_review" to lastReview
        )
    }

    // 打印卡片日志
    fun printLog() {
        try {
            val data = data()
            println(data)
        } catch (e: Exception) {
            println("Error serializing JSON: $e")
        }
    }
}

/**
 * 复习调度信息，包含卡片和复习日志
 */
class SchedulingInfo {

    var card: FSRSCard
    var reviewLog: ReviewLog

    // 直接传入卡片和日志
    constructor(card: FSRSCard, reviewLog: ReviewLog) {
        this.card = card
        this.reviewLog = reviewLog
    }

    // 通过评分、参考卡片、当前卡片和复习时间生成日志
    constructor(rating: Rating, reference: FSRSCard, current: FSRSCard, review: LocalDateTime) {
        this.card = reference
        this.reviewLog = ReviewLog(rating, reference.scheduledDays, current.elapsedDays, review, current.status)
    }

    // 转为Map
    fun data(): Map<String, Map<String, Any>> {
        return mapOf(
            "log" to reviewLog.data(),
            "card" to card.data()
        )
    }
}

/**
 * 四种评分下的卡片快照
 */
class SchedulingCards {

    var again: FSRSCard
    var hard: FSRSCard
    var good: FSRSCard
    var easy: FSRSCard

    // 初始化时克隆四份卡片
    constructor(card: FSRSCard) {
        this.again = card.clone()
        this.hard = card.clone()
        this.good = card.clone()
        this.easy = card.clone()
    }

    /**
     * 根据当前卡片状态，更新四种评分下的卡片状态
     */
    fun updateStatus(status: Status) {
        if (status == Status.New) {
            // 新卡片，除了Easy外都进入Learning，Again多一次遗忘
            arrayOf(again, hard, good).forEach { it.status = Status.Learning }
            easy.status = Status.Review
            again.lapses += 1
        } else if (status == Status.Learning || status == Status.Relearning) {
            // 学习/重新学习中，Again/Hard保持，Good/Easy进入Review
            arrayOf(again, hard).forEach { it.status = status }
            arrayOf(good, easy).forEach { it.status = Status.Review }
        } else if (status == Status.Review) {
            // 复习中，Again进入Relearning并+1遗忘，其他保持Review
            again.status = Status.Relearning
            arrayOf(hard, good, easy).forEach { it.status = Status.Review }
            again.lapses += 1
        }
    }

    /**
     * 安排四种评分下的下次复习时间
     */
    fun schedule(now: LocalDateTime, hardInterval: Double, goodInterval: Double, easyInterval: Double) {
        again.scheduledDays = 0.0
        hard.scheduledDays = hardInterval
        good.scheduledDays = goodInterval
        easy.scheduledDays = easyInterval
        again.due = addTime(now, 5, TimeUnit.MINUTES)
        if (hardInterval > 0) {
            hard.due = addTime(now, hardInterval.toLong(), TimeUnit.DAYS)
        } else {
            hard.due = addTime(now, 10, TimeUnit.MINUTES)
        }
        good.due = addTime(now, goodInterval.toLong(), TimeUnit.DAYS)
        easy.due = addTime(now, easyInterval.toLong(), TimeUnit.DAYS)
    }

    // 辅助方法：给定时间加上指定单位的数值
    fun addTime(now: LocalDateTime, value: Long, unit: TimeUnit): LocalDateTime {
        return when (unit) {
            TimeUnit.MINUTES -> now.plusMinutes(value)
            TimeUnit.HOURS -> now.plusHours(value)
            TimeUnit.DAYS -> now.plusDays(value)
            else -> now
        }
    }

    /**
     * 记录四种评分下的调度信息
     */
    fun recordLog(card: FSRSCard, now: LocalDateTime): Map<Rating, SchedulingInfo> {
        return mapOf(
            Rating.Again to SchedulingInfo(Rating.Again, again, card, now),
            Rating.Hard to SchedulingInfo(Rating.Hard, hard, card, now),
            Rating.Good to SchedulingInfo(Rating.Good, good, card, now),
            Rating.Easy to SchedulingInfo(Rating.Easy, easy, card, now)
        )
    }

    // 转为Map
    fun data(): Map<String, Map<String, Any>> {
        return mapOf(
            "again" to again.data(),
            "hard" to hard.data(),
            "good" to good.data(),
            "easy" to easy.data()
        )
    }
}

/**
 * 算法参数
 */
class Params {
    var requestRetention: Double      // 期望记忆保持率
    var maximumInterval: Double       // 最大间隔天数
    var w: List<Double>               // 各种权重参数

    constructor() {
        this.requestRetention = 0.9
        this.maximumInterval = 36500.0
        this.w = listOf(
            0.4,  // Again初始稳定性
            0.6,  // Hard初始稳定性
            2.4,  // Good初始稳定性
            5.8,  // Easy初始稳定性
            4.93, // 初始难度
            0.94, // 难度调整系数
            0.86, // 难度变化系数
            0.01, // 均值回归系数
            1.49, // 稳定性增长系数
            0.14, // 稳定性增长幂
            0.94, // 稳定性增长指数
            2.18, // 遗忘后稳定性系数
            0.05, // 遗忘后稳定性幂
            0.34, // 遗忘后稳定性增长幂
            1.26, // 遗忘后稳定性指数
            0.29, // Hard惩罚
            2.61  // Easy奖励
        )
    }
}

/**
 * FSRS主算法类
 */
class FSRS {
    var p: Params

    constructor() {
        this.p = Params()
    }

    /**
     * 复习主流程，根据卡片状态和评分，计算下次复习时间和参数
     * @param card 当前卡片
     * @param now 当前时间
     * @return 四种评分下的调度信息
     */
    fun `repeat`(card: FSRSCard, now: LocalDateTime): Map<Rating, SchedulingInfo> {
        val card = card.clone() as FSRSCard

        if (card.status == Status.New) {
            card.elapsedDays = 0.0
        } else {
            card.elapsedDays = max(
                0.0,
                ChronoUnit.DAYS.between(card.lastReview, now).toDouble()
            )
        }

        println("Elapsed ${card.elapsedDays}")
        card.lastReview = now
        card.reps += 1

        val s = SchedulingCards(card)
        s.updateStatus(card.status)

        if (card.status == Status.New) {
            // 新卡片，初始化难度和稳定性，安排首次复习
            initDS(s)
            s.again.due = s.addTime(now, 1, TimeUnit.MINUTES)
            s.hard.due = s.addTime(now, 5, TimeUnit.MINUTES)
            s.good.due = s.addTime(now, 10, TimeUnit.MINUTES)
            val easyInterval = nextInterval(s.easy.stability)
            s.easy.scheduledDays = easyInterval
            s.easy.due = s.addTime(now, easyInterval.toLong(), TimeUnit.DAYS)
        } else if (card.status == Status.Learning || card.status == Status.Relearning) {
            // 学习/重新学习，安排下次复习
            val hardInterval = 0.0
            val goodInterval = nextInterval(s.good.stability)
            val easyInterval = max(nextInterval(s.easy.stability), goodInterval + 1)
            s.schedule(now, hardInterval, goodInterval, easyInterval)
        } else if (card.status == Status.Review) {
            // 复习中，根据记忆曲线调整参数
            val interval = card.elapsedDays
            val lastDifficulty = card.difficulty
            val lastStability = card.stability
            // 计算当前回忆概率
            val retrievability = (1 + interval / (9 * lastStability)).pow(-1.0)
            // 计算四种评分下的难度和稳定性
            nextDS(s, lastDifficulty, lastStability, retrievability)
            var hardInterval = nextInterval(s.hard.stability)
            var goodInterval = nextInterval(s.good.stability)
            hardInterval = min(hardInterval, goodInterval)
            goodInterval = max(goodInterval, hardInterval + 1)
            val easyInterval = max(nextInterval(s.easy.stability), goodInterval + 1)
            s.schedule(now, hardInterval, goodInterval, easyInterval)
        }
        return s.recordLog(card, now)
    }

    /**
     * 初始化四种评分下的难度和稳定性
     */
    fun initDS(s: SchedulingCards) {
        s.again.difficulty = initDifficulty(Rating.Again)
        s.again.stability = initStability(Rating.Again)
        s.hard.difficulty = initDifficulty(Rating.Hard)
        s.hard.stability = initStability(Rating.Hard)
        s.good.difficulty = initDifficulty(Rating.Good)
        s.good.stability = initStability(Rating.Good)
        s.easy.difficulty = initDifficulty(Rating.Easy)
        s.easy.stability = initStability(Rating.Easy)
    }

    /**
     * 计算四种评分下的下一个难度和稳定性
     */
    fun nextDS(s: SchedulingCards, lastDifficulty: Double, lastStability: Double, retrievability: Double) {
        s.again.difficulty = nextDifficulty(lastDifficulty, Rating.Again)
        s.again.stability = nextForgetStability(s.again.difficulty, lastStability, retrievability)
        s.hard.difficulty = nextDifficulty(lastDifficulty, Rating.Hard)
        s.hard.stability = nextRecallStability(s.hard.difficulty, lastStability, retrievability, Rating.Hard)
        s.good.difficulty = nextDifficulty(lastDifficulty, Rating.Good)
        s.good.stability = nextRecallStability(s.good.difficulty, lastStability, retrievability, Rating.Good)
        s.easy.difficulty = nextDifficulty(lastDifficulty, Rating.Easy)
        s.easy.stability = nextRecallStability(s.easy.difficulty, lastStability, retrievability, Rating.Easy)
    }

    // 获取初始稳定性
    fun initStability(rating: Rating): Double {
        return initStability(rating.value())
    }

    fun initStability(r: Int): Double {
        return max(p.w[r - 1], 0.1)
    }

    // 获取初始难度
    fun initDifficulty(rating: Rating): Double {
        return initDifficulty(rating.value())
    }

    fun initDifficulty(r: Int): Double {
        return min(max(p.w[4] - p.w[5] * (r - 3), 1.0), 10.0)
    }

    // 由稳定性计算下次复习间隔
    fun nextInterval(s: Double): Double {
        val interval = s * 9 * (1 / p.requestRetention - 1)
        return min(max(round(interval), 1.0), p.maximumInterval)
    }

    // 计算下一个难度
    fun nextDifficulty(d: Double, rating: Rating): Double {
        val r = rating.value()
        val nextD = d - p.w[6] * (r - 3)
        return min(max(meanReversion(p.w[4], nextD), 1.0), 10.0)
    }

    // 均值回归，防止难度偏离过大
    fun meanReversion(initial: Double, current: Double): Double {
        return p.w[7] * initial + (1 - p.w[7]) * current
    }

    // 回忆成功后的稳定性增长
    fun nextRecallStability(d: Double, s: Double, r: Double, rating: Rating): Double {
        val hardPenalty = if (rating == Rating.Hard) p.w[15] else 1.0
        val easyBonus = if (rating == Rating.Easy) p.w[16] else 1.0
        return s * (1 + exp(p.w[8]) * (11 - d) * s.pow(-p.w[9]) * (exp((1 - r) * p.w[10]) - 1) * hardPenalty * easyBonus)
    }

    // 回忆失败后的稳定性
    fun nextForgetStability(d: Double, s: Double, r: Double): Double {
        return p.w[11] * d.pow(-p.w[12]) * ((s + 1.0).pow(p.w[13]) - 1) * exp((1 - r) * p.w[14])
    }
}
