import { getNotificationEmail, getTimetableMetadata } from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import type { PageLoad } from '../../$types';

export const load = (async () => {
  try {
    const { email } = await getNotificationEmail();
    const metadata = await getTimetableMetadata();
    return {
      email,
      metadata,
      meta: {
        title: 'Einstellungen',
      },
    };
  } catch {
    error(400, { message: `Could not fetch the timetable metadata` });
  }
}) satisfies PageLoad;
