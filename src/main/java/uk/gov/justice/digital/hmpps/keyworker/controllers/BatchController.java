package uk.gov.justice.digital.hmpps.keyworker.controllers;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.digital.hmpps.keyworker.batch.EnableNewNomisRoute;
import uk.gov.justice.digital.hmpps.keyworker.dto.ErrorResponse;

import java.util.List;

import static uk.gov.justice.digital.hmpps.keyworker.batch.DeallocationRoute.DIRECT_DEALLOCATION;
import static uk.gov.justice.digital.hmpps.keyworker.batch.PrisonStatsRoute.DIRECT_PRISON_STATS;
import static uk.gov.justice.digital.hmpps.keyworker.batch.UpdateStatusRoute.DIRECT_UPDATE_STATUS;

@Api(tags = {"batch"}, hidden = true)
@RestController
@RequestMapping(
        value="batch",
        produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@ConditionalOnProperty(name = "quartz.enabled")
public class BatchController {

    private final ProducerTemplate producerTemplate;

    public BatchController(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }


    @ApiOperation(
            value = "Enable Users access to New Nomis prison by prison batch process",
            notes = "Can only be run with SYSTEM_USER role",
            nickname = "runEnableNewNomisBatch",
            authorizations = { @Authorization("SYSTEM_USER") },
            hidden = true)

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Unrecoverable error occurred whilst processing request.", response = ErrorResponse.class) })

    @PostMapping(path = "/add-users-to-new-nomis")
    @PreAuthorize("hasRole('SYSTEM_USER')")
    public void runEnableNewNomisBatch() {
        producerTemplate.send(EnableNewNomisRoute.ENABLE_NEW_NOMIS, exchange -> {});
    }

    @ApiOperation(
            value = "Generate prison stats at specified prison.",
            notes = "Can only be run with SYSTEM_USER role",
            nickname = "runBatchPrisonStats",
            authorizations = { @Authorization("SYSTEM_USER") },
            hidden = true)

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Invalid request.", response = ErrorResponse.class ),
            @ApiResponse(code = 404, message = "Requested resource not found.", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Unrecoverable error occurred whilst processing request.", response = ErrorResponse.class) })

    @PostMapping(path = "/generate-stats")
    @PreAuthorize("hasRole('SYSTEM_USER')")
    public void runBatchPrisonStats() {
        producerTemplate.send(DIRECT_PRISON_STATS, exchange -> {});
    }

    @ApiOperation(
            value = "Checks for non active keyworkers with a reached active_date and updates the status to active",
            notes = "Can only be run with SYSTEM_USER role",
            hidden = true,
            authorizations = { @Authorization("SYSTEM_USER") },
            nickname="runBatchUpdateStatus")

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Update status process complete", response = String.class),
            @ApiResponse(code = 500, message = "Unrecoverable error occurred whilst processing request.", response = ErrorResponse.class) })

    @PostMapping(path = "/update-status")
    @PreAuthorize("hasRole('SYSTEM_USER')")
    public List<Long> runBatchUpdateStatus() {
        Exchange response = producerTemplate.send(DIRECT_UPDATE_STATUS, exchange -> {});
        List<Long> ids = response.getIn().getBody(List.class);
        log.info("processed /batch/updateStatus call. The following key workers have been set to status active: {}", ids.size());
        return ids;
    }

    /* --------------------------------------------------------------------------------*/

    @ApiOperation(
            value = "Force Runs the Batch De-allocation process",
            notes = "Can only be run with SYSTEM_USER role",
            authorizations = { @Authorization("SYSTEM_USER") },
            nickname="runBatchDeallocation",
            hidden = true)

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Batch process complete", response = String.class),
            @ApiResponse(code = 500, message = "Unrecoverable error occurred whilst processing request.", response = ErrorResponse.class) })

    @PostMapping(path = "/deallocate")
    @PreAuthorize("hasRole('SYSTEM_USER')")
    public void runBatchDeallocation() {
        producerTemplate.send(DIRECT_DEALLOCATION, exchange -> {});
    }

}
