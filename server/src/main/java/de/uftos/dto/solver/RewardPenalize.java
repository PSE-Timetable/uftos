package de.uftos.dto.solver;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Describes whether a constraint instance is soft or hard
 * and whether the constraint gets rewarded or penalized.
 */
@Schema(enumAsRef = true)
public enum RewardPenalize {
  SOFT_REWARD,
  HARD_REWARD,
  SOFT_PENALIZE,
  HARD_PENALIZE
}
