import { getSubject, getTags, type Sort, type Subject } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async ({ params }) => {
  const sort: Sort = { sort: ['name,asc'] };
  const tags = await getTags(sort);
  if (params.id === 'new') {
    const subject: Subject = { id: 'new', name: '', tags: [] };
    return {
      subject,
      tags,
      create: true,
      meta: {
        title: 'Fach — Hinzufügen',
      },
    };
  }
  try {
    const subject = await getSubject(params.id);
    return {
      subject,
      tags,
      create: false,
      meta: {
        title: `Fach — ${subject.name}`,
      },
    };
  } catch {
    error(404, { message: `Subject with id ${params.id} not found` });
  }
}) satisfies PageLoad;
