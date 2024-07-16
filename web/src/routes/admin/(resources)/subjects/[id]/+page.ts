import { getSubject } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async ({ params }) => {
  try {
    const subject = await getSubject(params.id);
    return {
      subject,
      meta: {
        title: `Subject — ${subject.name}`,
      },
    };
  } catch {
    error(404, { message: `Subject with id ${params.id} not found` });
  }
}) satisfies PageLoad;
