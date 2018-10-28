package uk.gov.justice.digital.hmpps.keyworker.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.*;

@ApiModel(description = "Prison")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(of = "prisonId")
public class Prison {
    private String prisonId;
    private boolean supported;
    private boolean migrated;
    private boolean autoAllocatedSupported;
    private int capacityTier1;
    private int capacityTier2;
    private int kwSessionFrequencyInWeeks;
}
