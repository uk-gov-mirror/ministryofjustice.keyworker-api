package uk.gov.justice.digital.hmpps.keyworker.services;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;
import uk.gov.justice.digital.hmpps.keyworker.dto.*;
import uk.gov.justice.digital.hmpps.keyworker.model.OffenderKeyworker;
import uk.gov.justice.digital.hmpps.keyworker.repository.OffenderKeyworkerRepository;

import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class KeyworkerService extends Elite2ApiSource {
    private static final ParameterizedTypeReference<List<KeyworkerAllocationDetailsDto>> KEYWORKER_ALLOCATION_LIST =
            new ParameterizedTypeReference<List<KeyworkerAllocationDetailsDto>>() {};

    private static final ParameterizedTypeReference<List<KeyworkerDto>> KEYWORKER_DTO_LIST =
            new ParameterizedTypeReference<List<KeyworkerDto>>() {};

    private static final ParameterizedTypeReference<List<OffenderSummaryDto>> OFFENDER_SUMMARY_DTO_LIST =
            new ParameterizedTypeReference<List<OffenderSummaryDto>>() {};

    private static final HttpHeaders CONTENT_TYPE_APPLICATION_JSON = httpContentTypeHeaders(MediaType.APPLICATION_JSON);

    private static HttpHeaders httpContentTypeHeaders(MediaType contentType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(contentType);
        return httpHeaders;
    }

    private final OffenderKeyworkerRepository repository;

    public KeyworkerService(OffenderKeyworkerRepository repository) {
        this.repository = repository;
    }

    public List<KeyworkerDto> getAvailableKeyworkers(String agencyId) {

        URI uri = new UriTemplate("/key-worker/{agencyId}/available").expand(agencyId);

        ResponseEntity<List<KeyworkerDto>> responseEntity = getForList(uri, KEYWORKER_DTO_LIST);

        return responseEntity.getBody();
    }

    public Page<KeyworkerAllocationDetailsDto> getKeyworkerAllocations(AllocationsFilterDto allocationFilter, PagingAndSortingDto pagingAndSorting) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/key-worker/{agencyId}/allocations");

        allocationFilter.getAllocationType().ifPresent(at -> builder.queryParam("allocationType", at.toString()));
        allocationFilter.getFromDate().ifPresent(fd -> builder.queryParam("fromDate", fd.format(DateTimeFormatter.ISO_DATE)));

        builder.queryParam("toDate", allocationFilter.getToDate().format(DateTimeFormatter.ISO_DATE));

        URI uri = builder.buildAndExpand(allocationFilter.getAgencyId()).toUri();

        ResponseEntity<List<KeyworkerAllocationDetailsDto>> response = getWithPagingAndSorting(uri, pagingAndSorting, KEYWORKER_ALLOCATION_LIST);

        return new Page<>(response.getBody(), response.getHeaders());
    }

    public Page<OffenderSummaryDto> getUnallocatedOffenders(String agencyId, PagingAndSortingDto pagingAndSorting) {

        URI uri = new UriTemplate("/key-worker/{agencyId}/offenders/unallocated").expand(agencyId);

        ResponseEntity<List<OffenderSummaryDto>> response = getWithPagingAndSorting(uri, pagingAndSorting, OFFENDER_SUMMARY_DTO_LIST);

        return new Page<>(response.getBody(), response.getHeaders());
    }

    public KeyworkerDto getKeyworkerDetails(Long staffId) {

        URI uri = new UriTemplate("/key-worker/{staffId}").expand(staffId);

        return restTemplate.getForObject(uri.toString(), KeyworkerDto.class);
    }

    @PreAuthorize("#oauth2.hasScope('write')")
    public String startAutoAllocation(String agencyId) {

        URI uri = new UriTemplate("/key-worker/{agencyId}/allocate/start").expand(agencyId);

        return restTemplate.exchange(
                uri.toString(),
                HttpMethod.POST,
                new HttpEntity<>(null, CONTENT_TYPE_APPLICATION_JSON),
                String.class).getBody();
    }

    @PreAuthorize("#oauth2.hasScope('write')")
    public void allocate(KeyworkerAllocationDto keyworkerAllocation) {

        restTemplate.postForObject(
                "/key-worker/allocate",
                new HttpEntity<>(keyworkerAllocation, CONTENT_TYPE_APPLICATION_JSON),
                Void.class);
    }

    public List<OffenderKeyworker> getAllocationHistoryForPrisoner(String offenderNo) {
        return repository.findByOffenderNo(offenderNo);
    }

    public List<OffenderKeyworker> getAllocationsForKeyworker(Long staffId) {
        return repository.findByStaffId(staffId);
    }
}
