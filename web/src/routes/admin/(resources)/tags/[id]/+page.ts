import { getTag } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async ({ params }) => {
  try {
    const tag = await getTag(params.id);
    return {
      tag,
      meta: {
        title: `Tag — ${tag.name}`,
      },
    };
  } catch {
    error(404, { message: `Tag with id ${params.id} not found` });
  }
}) satisfies PageLoad;
