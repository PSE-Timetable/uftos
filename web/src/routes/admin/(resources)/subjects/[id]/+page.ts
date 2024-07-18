import { getSubject, type Subject } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async ({ params }) => {
  if (params.id === 'new') {
    const subject: Subject = { id: 'new', name: '', tags: [] };
    return {
      subject,
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
      create: false,
      meta: {
        title: `Fach — ${subject.name}`,
      },
    };
  } catch {
    error(404, { message: `Subject with id ${params.id} not found` });
  }
}) satisfies PageLoad;
