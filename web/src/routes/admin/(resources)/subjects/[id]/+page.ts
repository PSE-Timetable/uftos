import { getSubject, getTags, type Sort } from '$lib/sdk/fetch-client';
import { init } from '$lib/utils/server';
import { error } from '@sveltejs/kit';
import { superValidate } from 'sveltekit-superforms';
import { zod } from 'sveltekit-superforms/adapters';
import { z } from 'zod';
import type { PageLoad } from './$types';

export const _schema = z.object({
  id: z.string(),
  name: z.string().trim().min(1, { message: 'Der Name darf nicht leer sein.' }),
  color: z.string().optional().default('#EDB686'),
  tags: z.string().array(),
});

export const load = (async ({ params }) => {
  init();
  const sort: Sort = { sort: ['name,asc'] };
  const tags = await getTags(sort);
  let formSubject: { id: string; name: string; tags: string[] }, title: string;
  if (params.id === 'new') {
    formSubject = { id: 'new', name: '', tags: [] };
    title = 'Fach — Hinzufügen';
  } else {
    try {
      const subject = await getSubject(params.id);
      formSubject = { ...subject, tags: subject.tags.map((tag) => tag.id) };
      title = `Fach — ${subject.name}`;
    } catch {
      error(404, { message: `Subject with id ${params.id} not found` });
    }
  }
  return {
    form: await superValidate(formSubject, zod(_schema), { errors: false }),
    tags,
    meta: {
      title,
    },
  };
}) satisfies PageLoad;
