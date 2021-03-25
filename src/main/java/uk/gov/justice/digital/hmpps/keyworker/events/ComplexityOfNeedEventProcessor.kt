package uk.gov.justice.digital.hmpps.keyworker.events

import com.google.gson.Gson
import com.microsoft.applicationinsights.TelemetryClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.keyworker.services.KeyworkerService
import java.time.LocalDateTime

data class ComplexityOfNeedChange(
  override val eventType: String,
  override val version: String,
  override val apiEndpoint: String,
  override val eventOccurred: LocalDateTime,
  val offenderNo: String,
  val level: String
) : DomainEvent

@Service
class ComplexityOfNeedEventProcessor(
  private val keyworkerService: KeyworkerService,
  private val telemetryClient: TelemetryClient,
  @Qualifier("gson") private val gson: Gson
) {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  fun onComplexityChange(message: String) {
    val event = gson.fromJson(message, ComplexityOfNeedChange::class.java)
    val complexityLevel = ComplexityOfNeedLevel.valueOf(event.level.toUpperCase())

    telemetryClient.trackEvent(
      "complexity-of-need-change",
      mapOf(
        "offenderNo" to event.offenderNo,
        "level-changed-to" to complexityLevel.toString()
      ),
      null
    )

    if (complexityLevel != ComplexityOfNeedLevel.HIGH) return

    log.info("Deallocating an offender based on their HIGH complexity of need")
    keyworkerService.deallocate(event.offenderNo)
  }
}