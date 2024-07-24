import { getTag, type Tag } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async ({ params }) => {
  if (params.id === 'new') {
    const tag: Tag = { id: 'new', name: '' };
    return {
      tag,
      create: true,
      meta: {
        title: 'Tag — Hinzufügen',
      },
    };
  }
  try {
    const tag = await getTag(params.id);
    return {
      tag,
      create: true,
      meta: {
        title: `Tag — ${tag.name}`,
      },
    };
  } catch {
    error(404, { message: `Tag with id ${params.id} not found` });
  }
}) satisfies PageLoad;
