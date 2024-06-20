package de.uftos.dto.solver;

/**
 * Describes whether a constraint instance is soft or hard
 * and whether the constraint gets rewarded or penalized.
 */
public enum RewardPenalize {
  SOFT_REWARD,
  HARD_REWARD,
  SOFT_PENALIZE,
  HARD_PENALIZE
}
