package lila.user

import scala.concurrent.duration._

import org.joda.time.DateTime
import play.api.libs.json.JsObject

import lila.db.api.$count
import lila.memo.{ AsyncCache, ExpireSetMemo }
import tube.userTube

final class Cached(
    nbTtl: Duration,
    onlineUserIdMemo: ExpireSetMemo) {

  val count = AsyncCache((o: JsObject) => $count(o), timeToLive = nbTtl)

  def countEnabled: Fu[Int] = count(UserRepo.enabledSelect)

  private def oneDayAgo = DateTime.now minusDays 1
  private def oneWeekAgo = DateTime.now minusWeeks 1
  private def oneMonthAgo = DateTime.now minusMonths 1

  val topRatingDay = AsyncCache(
    (nb: Int) => UserRepo.topRatingSince(oneDayAgo, nb),
    timeToLive = 13 minutes)
  val topRatingWeek = AsyncCache(
    (nb: Int) => UserRepo.topRatingSince(oneWeekAgo, nb),
    timeToLive = 28 minutes)
  val topRating = AsyncCache(UserRepo.topRating, timeToLive = 30 minutes)
  val topBullet = AsyncCache(UserRepo.topBullet, timeToLive = 31 minutes)
  val topBlitz = AsyncCache(UserRepo.topBlitz, timeToLive = 32 minutes)
  val topClassical = AsyncCache(UserRepo.topClassical, timeToLive = 33 minutes)
  val topChess960 = AsyncCache(UserRepo.topChess960, timeToLive = 36 minutes)
  val topPool1_0 = AsyncCache(UserRepo.topPool1_0, timeToLive = 31 minutes)
  val topNbGame = AsyncCache(UserRepo.topNbGame, timeToLive = 34 minutes)

  val topPool = AsyncCache(
    (poolIdAndNb: (String, Int)) => UserRepo.topPool(poolIdAndNb._1, poolIdAndNb._2),
    timeToLive = 20 minutes)

  val topOnline = AsyncCache(
    (nb: Int) => UserRepo.byIdsSortRating(onlineUserIdMemo.keys, nb),
    timeToLive = 2 seconds)

  val topToints = AsyncCache(
    (nb: Int) => UserRepo allSortToints nb,
    timeToLive = 10 minutes)
}
