import { defaults } from '$lib/sdk/fetch-client';
import { memoize } from 'lodash-es';

type Fetch = typeof fetch;

function _init(fetch: Fetch) {
  defaults.fetch = fetch;
}

export const init = memoize(_init, () => 'singlevalue');
