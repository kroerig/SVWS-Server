import type { JavaSet } from './JavaSet';
import type { Collection } from './Collection';
import type { JavaIterator } from './JavaIterator';

import { JavaMapEntry } from './JavaMapEntry';
import { JavaObject } from '../../java/lang/JavaObject';
import { UnsupportedOperationException } from '../lang/UnsupportedOperationException';

export class HashMapEntrySet<K, V> extends JavaObject implements JavaSet<JavaMapEntry<K, V>> {

	protected readonly _map : Map<K, JavaMapEntry<K, V>>;

	public constructor(map : Map<K, JavaMapEntry<K, V>>) {
		super();
		this._map = map;
	}

	size(): number {
		return this._map.size;
	}

	isEmpty(): boolean {
		return this._map.size === 0;
	}

	contains(entry: any): boolean {
		for (const [k, e] of this._map) {
			if (e === entry)
				return true;
			if ((e instanceof JavaMapEntry) && (e.equals(entry)))
				return true;
		}
		return false;
	}

	iterator(): JavaIterator<JavaMapEntry<K, V>> {
		const it = this[Symbol.iterator]()
		let next_item = it.next()
		return {
			hasNext():boolean {
				return next_item.done ? false : true
			},
			next(): JavaMapEntry<K, V> {
				const e = next_item.value
				next_item = it.next()
				return e;
			},
			remove() {
			},
		}
	}

	public toArray() : Array<unknown>;
	public toArray<U>(a: Array<U>) : Array<U>;
	public toArray<T>(__param0? : Array<T>) : Array<T> | Array<unknown> {
		if ((typeof __param0 === "undefined") || (__param0 == null) || (__param0.length < this.size())) {
			const r : Array<JavaMapEntry<K,V>> = [];
			for (const [k, e] of this._map)
				r.push(e);
			return r;
		} else if (Array.isArray(__param0)) {
			// TODO handle the case where a is not null and try to fill into the parameter array if possible - see JavaDoc for implementation
			throw new Error('not yet implemented')
		} else throw new Error('invalid method overload');
	}

	add(e: JavaMapEntry<K, V> | null): boolean {
		throw new UnsupportedOperationException();
	}

	remove(e: any): boolean {
		throw new UnsupportedOperationException();
	}

	containsAll(c: Collection<any> | null): boolean {
		throw new UnsupportedOperationException();
	}

	addAll(c: Collection<JavaMapEntry<K, V>> | null): boolean {
		throw new UnsupportedOperationException();
	}

	removeAll(c: Collection<any> | null): boolean {
		throw new UnsupportedOperationException();
	}

	retainAll(c: Collection<any> | null): boolean {
		throw new UnsupportedOperationException();
	}

	clear(): void {
		this._map.clear();
	}

	[Symbol.iterator](): Iterator<JavaMapEntry<K, V>, any, undefined> {
		return this._map.values();
	}

}