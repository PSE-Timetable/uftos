import { getConstraintSignature } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async ({ params }) => {
  try {
    const constraintSignature = await getConstraintSignature(params.signatureId);
    return {
      constraintSignature,
      meta: {
        title: `Constraint Signature — ${constraintSignature.name}`,
      },
    };
  } catch {
    error(404, { message: `Constraint signature with id ${params.signatureId} not found` });
  }
}) satisfies PageLoad;
