import { defaults } from '$lib/sdk/fetch-client';
import { memoize } from 'lodash-es';

function _init() {
  //   defaults.fetch = fetch;
  defaults.baseUrl = 'http://localhost:5173/api';
}

export const init = memoize(_init, () => 'singlevalue');
