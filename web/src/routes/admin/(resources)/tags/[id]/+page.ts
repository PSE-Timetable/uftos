import { getTag } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import { superValidate } from 'sveltekit-superforms';
import { zod } from 'sveltekit-superforms/adapters';
import { z } from 'zod';
import type { PageLoad } from './$types';

export const _schema = z.object({
  id: z.string(),
  name: z.string().trim().min(1, { message: 'Der Name darf nicht leer sein.' }),
});

export const load = (async ({ params }) => {
  let tag, title;
  if (params.id === 'new') {
    tag = { id: 'new', name: '' };
    title = 'Tag — Hinzufügen';
  } else {
    try {
      tag = await getTag(params.id);
      title = `Tag — ${tag.name}`;
    } catch {
      error(404, { message: `Tag with id ${params.id} not found` });
    }
  }
  return {
    form: await superValidate(tag, zod(_schema), { errors: false }),
    meta: {
      title,
    },
  };
}) satisfies PageLoad;
