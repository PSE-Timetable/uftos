import { getServerStats } from '$lib/sdk/fetch-client';
import { loadGrades, loadRoomPage, loadStudentPage, loadTeacherPage } from '$lib/utils/resources';
import { init } from '$lib/utils/server';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load = (async ({ fetch }) => {
  init(fetch);
  try {
    return {
      initialGrades: await loadGrades('', ''),
      initialRooms: await loadRoomPage('', '', 0, 5),
      initialStudents: await loadStudentPage('', '', 0, 5),
      initialTeachers: await loadTeacherPage('', '', 0, 5),
      stats: await getServerStats(),
      meta: {
        title: 'Übersicht',
      },
    };
  } catch (e) {
    console.log(e);
    error(400, { message: 'Could not fetch page' });
  }
}) satisfies PageLoad;
