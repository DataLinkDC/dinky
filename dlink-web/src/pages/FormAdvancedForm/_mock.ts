// eslint-disable-next-line import/no-extraneous-dependencies
import type { Request, Response } from 'express';

export default {
  'POST  /api/forms': (_: Request, res: Response) => {
    res.send({ message: 'Ok' });
  },
};
