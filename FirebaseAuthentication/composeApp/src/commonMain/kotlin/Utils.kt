import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class Utils {

    companion object {
        fun currentTime(): String {
            val datetime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val hour = if (datetime.hour < 10) "0${datetime.hour}" else datetime.hour
            val minute = if (datetime.minute < 10) "0${datetime.minute}" else datetime.minute
            return "${datetime.dayOfMonth}-${datetime.month.name}-${datetime.year}/${hour}:${minute}"
        }
    }
}